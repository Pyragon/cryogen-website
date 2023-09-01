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

let requestFunctions = {
    get: instance.get,
    post: instance.post,
    put: instance.put,
    delete: instance.delete,
}

let timeoutError = false;

function request(requestFunction, path, params) {
    return new Promise(async(resolve, reject) => {
        try {

            let res = await requestFunction(path, params);
            timeoutError = false;
            if (res.data.error) {
                reject(res.data.error);
                return;
            }
            resolve(res);

        } catch (error) {
            if (timeoutError) {
                reject('');
                return;
            }
            if (error.message && error.message.includes('timeout')) {
                timeoutError = true;
                console.error('Timed out while requesting ' + path);
                reject('It looks like the server has timed out. Please wait for it to come back online.');
                return;
            }
            let message = 'Error requesting from server. Please try again or report this message if it repeats.';
            if (error.response)
                message = error.response.data.error;
            if (!message)
                message = 'Error requesting from server. Please try again or report this message if it repeats.';
            reject(message);
        }
    });
}

function get(path, params) {
    return request(requestFunctions.get, path, params);
}

function post(path, params) {
    return request(requestFunctions.post, path, params);
}

function put(path, params) {
    return request(requestFunctions.put, path, params);
}

function del(path, params) {
    return request(requestFunctions.delete, path, params);
}

export default {get, post, put, delete: del };