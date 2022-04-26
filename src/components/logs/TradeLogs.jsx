import React, { useContext, useState, useEffect } from 'react';
import axios from '../../utils/axios';

import PageContext from '../../utils/contexts/PageContext';
import NotificationContext from '../../utils/contexts/NotificationContext';

import TableSection from '../utils/sections/TableSection';
import Pages from '../utils/Pages';

const info = [

];

export default function TradeLogs() {
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
				type: 'user',
				value: log.user2,
			},
			{
				type: 'items',
				value: log.items,
			},
			{
				type: 'items',
				value: log.items2,
			},
			{
				type: 'text',
				value: log.ip,
			},
			{
				type: 'text',
				value: log.extra.tradeeIp,
			},
			{
				type: 'tile',
				value: log.tile,
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

				let res = await axios.get(`/logs/${page}?type=trade`);

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
				headers={['Trader', 'Tradee', 'Trader Items', 'Tradee Items', 'Trader IP', 'Tradee IP', 'Tile', 'Date']}
				rows={rows}
			/>
			<Pages
				pageTotal={pageTotal}
				base='/staff/logs/trade'
			/>
		</>
	)
}
