const axios = require('axios');

const instance = axios.create({
    baseURL: 'http://localhost:8081/',
    timeout: 1000,
    headers: { 'Authorization': localStorage.getItem('sessionId') || 'test' }
});

export default instance;