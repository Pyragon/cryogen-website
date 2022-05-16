import React, { useState, useContext, useEffect } from 'react';

import axios from '../../../utils/axios';

import ViewingUserContext from '../../../utils/contexts/ViewingUserContext';
import NotificationContext from '../../../utils/contexts/NotificationContext';
import PageContext from '../../../utils/contexts/PageContext';

import Table from '../../../components/utils/Table';
import Pages from '../../../components/utils/Pages';

export default function UserThreads() {
	let [ page, setPage ] = useState(1);
	let [ threads, setThreads ] = useState([]);
	let [ pageTotal, setPageTotal ] = useState(1);

	let { viewingUser } = useContext(ViewingUserContext);

	let { sendErrorNotification } = useContext(NotificationContext);

	useEffect(() => {

		let loadThreads = async () => {
			
			try {

				let res = await axios.get(`/users/${viewingUser._id}/threads/${page}`);

				setThreads(res.data.threads);
				setPageTotal(res.data.pageTotal);

			} catch(error) {
				console.log(error);
				sendErrorNotification(error);
			}
			
		};

		loadThreads();

	}, [ page ]);

	let rows = threads.map(thread => {
		return [
			{
				type: 'link',
				value: thread.title,
				link: '/forums/threads/' + thread._id
			},
			{
				type: 'text',
				value: thread.views,
			},
			{
				type: 'text',
				value: thread.postCount + 1,
			},
			{
				type: 'date',
				value: thread.createdAt,
			}
		]
	});

	return (
		<>
			<PageContext.Provider value={{ page, setPage }}>
				<Table
					headers={[ 'Thread', 'Views', 'Replies', 'Date' ]}
					rows={rows}
				/>
				<Pages
					pageTotal={pageTotal}
				/>
			</PageContext.Provider>
		</>
	)
}
