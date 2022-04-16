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

			let res = await axios.get('/users/'+viewingUser._id+'/threads/'+page);
			if(res.data.error) {
				sendErrorNotification(res.data.error);
				return;
			}

			setThreads(res.data.threads);
			setPageTotal(res.data.pageTotal);
			
		};

		loadThreads();

	}, [ page ]);

	let rows = threads.map(thread => {
		return [
			{
				type: 'link',
				value: thread.title,
				link: '/forums/threads/' + thread.id
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

	//table displaying all the posts, and what threads they were posted in
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
