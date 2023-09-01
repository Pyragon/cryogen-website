import React, { useContext, useState, useEffect } from 'react';
import axios from '../../../utils/axios';

import UserContext from '../../../utils/contexts/UserContext';
import NotificationContext from '../../../utils/contexts/NotificationContext';

export default function About() {
	let { user } = useContext(UserContext);
	let { sendErrorNotification } = useContext(NotificationContext);
	if(!user.settings.about) {
		return (
			<p>This user has not added an about page yet.</p>
		)
	}

	let [ about, setAbout ] = useState(user.settings.about);

	// useEffect(() => {

	// 	let loadAbout = async () => {

	// 		try {

	// 			let res = await axios.get(`/users/${user._id}/about`);

	// 			setAbout(res.data.about);


	// 		} catch(error) {
	// 			sendErrorNotification(error);
	// 		}

	// 	};

	// 	loadAbout();

	// }, []);

	return (
		<p>{about}</p>
	)
}
