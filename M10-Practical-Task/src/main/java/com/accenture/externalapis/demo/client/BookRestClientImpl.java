package com.accenture.externalapis.demo.client;

import com.accenture.externalapis.demo.config.ExternalServiceProperties;
import com.accenture.externalapis.demo.dto.BookApiResponse;
import com.accenture.externalapis.demo.dto.BookDto;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class BookRestClientImpl implements BookRestClient {

    private final RestClient restClient;

    public BookRestClientImpl(RestClient.Builder builder, ExternalServiceProperties properties) {
        this.restClient = builder.baseUrl(properties.baseUrl()).build();
    }

    @Override
    public BookDto getBook(Long id) {
        try {
            BookApiResponse response = restClient.get()
                    .uri("/books/{id}", id)
                    .retrieve()
                    .body(BookApiResponse.class);
            return toBookDto(response);
        } catch (HttpClientErrorException e) {
            throw new ClientException("Book not found or bad request for id " + id, e);
        } catch (HttpServerErrorException e) {
            throw new ClientException("External service failed for id " + id, e);
        } catch (ResourceAccessException e) {
            throw new ClientException("External service is unreachable", e);
        } catch (RestClientException e) {
            throw new ClientException("Could not read the response for id " + id, e);
        }
    }

    @Override
    public List<BookDto> getAllBooks() {
        try {
            BookApiResponse[] response = restClient.get()
                    .uri("/books")
                    .retrieve()
                    .body(BookApiResponse[].class);

            List<BookDto> books = new ArrayList<>();
            for (BookApiResponse book : response) {
                books.add(toBookDto(book));
            }
            return books;
        } catch (HttpClientErrorException e) {
            throw new ClientException("Bad request when fetching all books", e);
        } catch (HttpServerErrorException e) {
            throw new ClientException("External service failed when fetching all books", e);
        } catch (ResourceAccessException e) {
            throw new ClientException("External service is unreachable", e);
        } catch (RestClientException e) {
            throw new ClientException("Could not read the response when fetching all books", e);
        }
    }

    private BookDto toBookDto(BookApiResponse book) {
        return new BookDto(book.title(), book.author(), book.genre(), book.price());
    }
}
