package com.accenture.externalapis.demo.client;

import com.accenture.externalapis.demo.config.ExternalServiceProperties;
import com.accenture.externalapis.demo.dto.BookApiResponse;
import com.accenture.externalapis.demo.dto.BookDto;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class BookWebClientImpl implements BookWebClient {

    private final WebClient webClient;

    public BookWebClientImpl(WebClient.Builder builder, ExternalServiceProperties properties) {
        this.webClient = builder.baseUrl(properties.baseUrl()).build();
    }

    @Override
    public Mono<BookDto> getBookAsync(Long id) {
        return webClient.get()
                .uri("/books/{id}", id)
                .retrieve()
                .bodyToMono(BookApiResponse.class)
                .map(this::toBookDto)
                .onErrorMap(WebClientResponseException.class,
                        e -> new ClientException("External service returned an error for id " + id, e))
                .onErrorMap(WebClientRequestException.class,
                        e -> new ClientException("External service is unreachable", e))
                .onErrorMap(e -> !(e instanceof ClientException),
                        e -> new ClientException("Could not read the response for id " + id, e));
    }

    @Override
    public Flux<BookDto> getAllBooksAsync() {
        return webClient.get()
                .uri("/books")
                .retrieve()
                .bodyToFlux(BookApiResponse.class)
                .map(this::toBookDto)
                .onErrorMap(WebClientResponseException.class,
                        e -> new ClientException("External service returned an error when fetching all books", e))
                .onErrorMap(WebClientRequestException.class,
                        e -> new ClientException("External service is unreachable", e))
                .onErrorMap(e -> !(e instanceof ClientException),
                        e -> new ClientException("Could not read the response when fetching all books", e));
    }

    @Override
    public Mono<List<BookDto>> getBooksInParallel(Long id1, Long id2) {
        Mono<BookDto> book1 = getBookAsync(id1);
        Mono<BookDto> book2 = getBookAsync(id2);
        return Mono.zip(book1, book2, (b1, b2) -> List.of(b1, b2));
    }

    private BookDto toBookDto(BookApiResponse book) {
        return new BookDto(book.title(), book.author(), book.genre(), book.price());
    }
}
