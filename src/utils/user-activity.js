import axios from './axios';

const setUserActivity = async(user, activity) => {
    if (!user || !activity) return;
    try {
        let results = await axios.post('/users/activity', { activity });
    } catch (err) {
        console.error(err);
    }
};

export default setUserActivity;