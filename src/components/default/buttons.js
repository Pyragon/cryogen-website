import axios from '../../utils/axios';

export default [{
        header: 'Cryogen',
        buttons: [{
                title: 'Download JAR',
                link: 'http://api.cryogen-rsps.com/live/download/latest',
                isATag: true
            },
            {
                title: 'Open-Sourced',
                link: 'http://github.com/Pyragon/cryogen-website',
                isATag: true
            },
            {
                title: 'API Documentation',
                link: '/api'
            }
        ]
    },
    {
        header: 'Community',
        buttons: [{
                title: 'Forums',
                link: '/forums'
            },
            {
                title: 'Highscores',
                link: '/highscores'
            },
            {
                title: 'Discord',
                link: 'http://discord.cryogen-rsps.com',
                isATag: true
            },
            {
                title: 'Movie Night',
                link: '/movie-night'
            },
            {
                title: 'Vote',
                link: '/vote'
            }
        ]
    },
    {
        header: 'Account',
        buttons: [{
                title: 'Support',
                link: '/support'
            },
            {
                title: 'Login',
                link: '/login',
                requiresLogin: false
            },
            {
                title: 'Register',
                link: '/register',
                requiresLogin: false
            },
            {
                title: 'Forgot my Password',
                link: '/forgot',
                requiresLogin: false
            },
            {
                title: 'Profile',
                link: '/user',
                requiresLogin: true
            },
            {
                title: 'Inbox',
                link: '/forums/private/inbox',
                requiresLogin: true
            },
            {
                title: 'Staff',
                link: '/staff',
                requiresLogin: true,
                requiresStaff: true
            },
            {
                title: 'Logout',
                link: '/logout',
                onClick: async(e, { setUser, sendErrorNotification }) => {

                    try {

                        await axios.post('/users/logout');

                        setUser(null);
                        localStorage.removeItem('sessionId');
                        sessionStorage.removeItem('sessionId');

                    } catch (error) {
                        sendErrorNotification(error);
                    }
                },
                requiresLogin: true,
                isATag: true,
            }
        ]
    },
    {
        header: 'Admin',
        requiresStaff: true,
        buttons: [{
                title: 'Logs',
                link: '/staff/logs'
            },
            {
                title: 'Forum Admin Panel',
                link: '/forums/admin'
            },
            {
                title: 'Default',
                link: '#',
                onClick: async(e, { sendNotification, sendErrorNotification }) => {

                },
                isATag: true
            },
            {
                title: 'Refresh BBCodes',
                link: '#',
                onClick: async(e, { sendNotification, sendErrorNotification }) => {

                    try {

                        await axios.post('/forums/bbcodes/refresh');

                        sendNotification({ text: 'BBCodes have been refreshed.' });

                    } catch (error) {
                        sendErrorNotification(error);
                    }
                },
                isATag: true
            }
        ]
    }
];