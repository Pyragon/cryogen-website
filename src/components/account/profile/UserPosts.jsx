import React, { useState, useEffect, useContext } from 'react';
import Pages from '../../../components/utils/Pages';

import Table from '../../../components/utils/Table';
import PageContext from '../../../utils/contexts/PageContext';
import ViewingUserContext from '../../../utils/contexts/ViewingUserContext';
import NotificationContext from '../../../utils/contexts/NotificationContext';

import axios from '../../../utils/axios';

export default function UserPosts({ user }) {
	let [ page, setPage ] = useState(1);
	let [ posts, setPosts ] = useState([]);
	let [ pageTotal, setPageTotal ] = useState(1);

	let { viewingUser } = useContext(ViewingUserContext);

	let { sendErrorNotification } = useContext(NotificationContext);

	useEffect(() => {

		//get user posts for page
		let loadPosts = async () => {

			try {

				let res = await axios.get(`/users/${viewingUser._id}/posts/${page}`);
	
				setPosts(res.data.posts);
				setPageTotal(res.data.pageTotal);

			} catch (err) {
				sendErrorNotification(err);
			}
			
		};

		loadPosts();

	}, [ page ]);

	let rows = posts.map(post => {
		return [
			{
				type: 'link',
				value: post.thread.title,
				link: '/forums/threads/' + post.thread.id
			},
			{
				type: 'text',
				value: post.thread.views,
			},
			{
				type: 'text',
				value: post.thread.postCount + 1,
			},
			{
				type: 'date',
				value: post.createdAt,
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
