const api = {
    list: () => fetch('/api/chats').then(r => r.json()),
    create: () => fetch('/api/chats', { method: 'POST' }).then(r => r.json()),
    get: (id) => fetch(`/api/chats/${id}`).then(r => r.json()),
    remove: (id) => fetch(`/api/chats/${id}`, { method: 'DELETE' }),
    send: (id, content) => fetch(`/api/chats/${id}/chatMessages`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ content })
    }).then(async r => {
        if (!r.ok) {
            const problem = await r.json().catch(() => ({}));
            throw new Error(problem.detail || 'Request failed');
        }
        return r.json();
    })
};

const els = {
    chatList: document.getElementById('chat-list'),
    messages: document.getElementById('chatMessages'),
    title: document.getElementById('chat-title'),
    input: document.getElementById('input'),
    send: document.getElementById('send'),
    form: document.getElementById('composer'),
    error: document.getElementById('error'),
    newChat: document.getElementById('new-chat')
};

let activeChatId = null;

function setError(message) {
    els.error.textContent = message || '';
}

function setComposerEnabled(enabled) {
    els.input.disabled = !enabled;
    els.send.disabled = !enabled;
}

async function refreshChatList() {
    const chats = await api.list();
    els.chatList.innerHTML = '';
    chats.forEach(chat => els.chatList.appendChild(renderChatItem(chat)));
}

function renderChatItem(chat) {
    const item = document.createElement('div');
    item.className = 'chat-item' + (chat.id === activeChatId ? ' active' : '');
    item.onclick = () => openChat(chat.id);

    const title = document.createElement('span');
    title.className = 'title';
    title.textContent = chat.title;

    const count = document.createElement('span');
    count.className = 'count';
    count.textContent = chat.messageCount;

    const del = document.createElement('button');
    del.className = 'del';
    del.textContent = '🗑';
    del.onclick = (e) => { e.stopPropagation(); deleteChat(chat.id); };

    item.append(title, count, del);
    return item;
}

function renderMessages(chat) {
    els.title.textContent = chat.title;
    els.messages.innerHTML = '';
    if (!chat.chatMessages.length) {
        els.messages.innerHTML = '<div class="empty">Say hello to start the conversation.</div>';
        return;
    }
    chat.chatMessages.forEach(m => els.messages.appendChild(renderMessage(m)));
    els.messages.scrollTop = els.messages.scrollHeight;
}

function renderMessage(message) {
    const wrapper = document.createElement('div');
    wrapper.className = `msg ${message.role.toLowerCase()}`;
    const role = document.createElement('div');
    role.className = 'role';
    role.textContent = message.role;
    const body = document.createElement('div');
    body.textContent = message.content;
    wrapper.append(role, body);
    return wrapper;
}

async function openChat(id) {
    setError('');
    activeChatId = id;
    const chat = await api.get(id);
    renderMessages(chat);
    setComposerEnabled(true);
    els.input.focus();
    await refreshChatList();
}

async function startNewChat() {
    setError('');
    const chat = await api.create();
    await refreshChatList();
    await openChat(chat.id);
}

async function deleteChat(id) {
    setError('');
    await api.remove(id);
    if (id === activeChatId) {
        activeChatId = null;
        els.title.textContent = 'Select or start a chat';
        els.messages.innerHTML = '<div class="empty">No conversation selected.</div>';
        setComposerEnabled(false);
    }
    await refreshChatList();
}

async function submitMessage(content) {
    setError('');
    setComposerEnabled(false);
    appendPending(content);
    try {
        const chat = await api.send(activeChatId, content);
        renderMessages(chat);
        await refreshChatList();
    } catch (err) {
        setError(err.message);
        const chat = await api.get(activeChatId);
        renderMessages(chat);
    } finally {
        setComposerEnabled(true);
        els.input.focus();
    }
}

function appendPending(content) {
    els.messages.querySelector('.empty')?.remove();
    els.messages.appendChild(renderMessage({ role: 'USER', content }));
    const thinking = renderMessage({ role: 'ASSISTANT', content: '…' });
    thinking.classList.add('pending');
    els.messages.appendChild(thinking);
    els.messages.scrollTop = els.messages.scrollHeight;
}

els.newChat.onclick = startNewChat;

els.form.addEventListener('submit', (e) => {
    e.preventDefault();
    const content = els.input.value.trim();
    if (!content || !activeChatId) return;
    els.input.value = '';
    submitMessage(content);
});

els.input.addEventListener('keydown', (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
        e.preventDefault();
        els.form.requestSubmit();
    }
});

refreshChatList();

// --- Tabs ---
document.querySelectorAll('.tabbar button').forEach(btn => {
    btn.onclick = () => {
        const name = btn.dataset.tab;
        document.querySelectorAll('.tabbar button').forEach(b => b.classList.toggle('active', b === btn));
        document.querySelectorAll('.panel').forEach(p => p.classList.toggle('active', p.id === `panel-${name}`));
    };
});

async function postJson(url, body) {
    const r = await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    });
    if (!r.ok) {
        const problem = await r.json().catch(() => ({}));
        throw new Error(problem.detail || 'Request failed');
    }
    return r.json();
}

// --- Movies ---
const movie = {
    request: document.getElementById('movie-request'),
    liked: document.getElementById('movie-liked'),
    go: document.getElementById('movie-go'),
    error: document.getElementById('movie-error'),
    results: document.getElementById('movie-results')
};

movie.go.onclick = async () => {
    const request = movie.request.value.trim();
    movie.error.textContent = '';
    if (!request) { movie.error.textContent = 'Please describe what you want to watch.'; return; }
    const liked = movie.liked.value.split(',').map(s => s.trim()).filter(Boolean);

    movie.go.disabled = true;
    movie.results.innerHTML = '<div class="empty">Thinking… two agents at work (~15–40s).</div>';
    try {
        const picks = await postJson('/api/movies/recommend', { request, liked });
        movie.results.innerHTML = '';
        if (!picks.length) { movie.results.innerHTML = '<div class="empty">No recommendations found.</div>'; return; }
        picks.forEach(m => {
            const card = document.createElement('div');
            card.className = 'card';
            const title = document.createElement('div');
            title.className = 'card-title';
            title.textContent = m.year ? `${m.title} (${m.year})` : m.title;
            const why = document.createElement('div');
            why.textContent = m.whyYoullLikeIt;
            card.append(title, why);
            movie.results.appendChild(card);
        });
    } catch (err) {
        movie.error.textContent = err.message;
        movie.results.innerHTML = '';
    } finally {
        movie.go.disabled = false;
    }
};

// --- RAG ---
const rag = {
    question: document.getElementById('rag-question'),
    go: document.getElementById('rag-go'),
    error: document.getElementById('rag-error'),
    answer: document.getElementById('rag-answer')
};

rag.go.onclick = async () => {
    const question = rag.question.value.trim();
    rag.error.textContent = '';
    if (!question) { rag.error.textContent = 'Please enter a question.'; return; }

    rag.go.disabled = true;
    rag.answer.innerHTML = '<div class="empty">Searching the knowledge base…</div>';
    try {
        const data = await postJson('/api/rag/ask', { question });
        rag.answer.innerHTML = '';
        const card = document.createElement('div');
        card.className = 'card';
        const body = document.createElement('div');
        body.textContent = data.answer;
        card.appendChild(body);
        if (data.sources && data.sources.length) {
            const sources = document.createElement('div');
            sources.className = 'sources';
            sources.textContent = 'Sources: ' + data.sources.join(', ');
            card.appendChild(sources);
        }
        rag.answer.appendChild(card);
    } catch (err) {
        rag.error.textContent = err.message;
        rag.answer.innerHTML = '';
    } finally {
        rag.go.disabled = false;
    }
};

rag.question.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') { e.preventDefault(); rag.go.click(); }
});
