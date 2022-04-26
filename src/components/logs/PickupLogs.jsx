import React, { useContext, useState, useEffect } from 'react';
import axios from '../../utils/axios';

import PageContext from '../../utils/contexts/PageContext';
import NotificationContext from '../../utils/contexts/NotificationContext';

import TableSection from '../utils/sections/TableSection';
import Pages from '../utils/Pages';

const info = [

];

export default function PickupLogs() {
	let { page } = useContext(PageContext);
	
	let [ pageTotal, setPageTotal ] = useState(1);
	let [ logs, setLogs ] = useState([]);
	let { sendErrorNotification } = useContext(NotificationContext);

	let rows = logs.map(log => {
		return [
			{
				type: 'user',
				value: log.user,
			},
			{
				type: 'text',
				value: log.extra.item.defs.name+' ('+log.extra.item.id+') x'+log.extra.item.amount,
			},
			{
				type: 'user',
				value: log.user2,
			},
			{
				type: 'tile',
				value: log.tile,
			},
			{
				type: 'text',
				value: log.ip,
			},
			{
				type: 'date',
				value: log.createdAt,
			}
		];
	});

	useEffect(() => {

		let loadPage = async () => {

			try {

				let res = await axios.get(`/logs/${page}?type=pickup`);

				setLogs(res.data.logs);
				setPageTotal(res.data.pageTotal);

			} catch(error) {
				sendErrorNotification(error);
			}
		};

		loadPage();
	}, [ page ]);
	return (
		<>
			<TableSection
				info={info}
				actions={[]}
				headers={['User', 'Item', 'Dropped By', 'Tile', 'IP', 'Date']}
				rows={rows}
			/>
			<Pages
				pageTotal={pageTotal}
				base='/staff/logs/pickup'
			/>
		</>
	)
}
