import axios from './axios';

const setUserActivity = async(user, activity, type, id) => {
    if (!user || !activity || !type) return;
    try {
        axios.post('/users/activity', { activity, type, id });
    } catch (err) {
        console.error(err);
    }
};

export default setUserActivity;