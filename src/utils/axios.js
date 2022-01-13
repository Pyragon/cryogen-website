const axios = require('axios');

const instance = axios.create({
    baseURL: 'http://localhost:8081/',
    timeout: 1000,
});

instance.interceptors.request.use(config => {
    let sessionId = localStorage.getItem('sessionId') || sessionStorage.getItem('sessionId');
    if (sessionId)
        config.headers.Authorization = sessionId;
    return config;
});

export default instance;