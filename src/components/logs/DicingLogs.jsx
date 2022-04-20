import React, { useContext, useState, useEffect } from 'react';
import axios from '../../utils/axios';

import PageContext from '../../utils/contexts/PageContext';
import NotificationContext from '../../utils/contexts/NotificationContext';

import TableSection from '../utils/sections/TableSection';
import Pages from '../utils/Pages';

const info = [

];

export default function DicingLogs() {
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
				type: 'tile',
				value: log.tile,
			},
			{
				type: 'user',
				value: log.extra.hostWon ? log.user : log.user2,
			},
			{
				type: 'text',
				value: log.extra.roll,
			},
			{
				type: 'items',
				value: log.items,
				short: true,
			},
			{
				type: 'items',
				value: log.items2,
				short: true,
			},
			{
				type: 'text',
				value: log.ip,
			},
			{
				type: 'text',
				value: log.extra.playerIp
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
				let res = await axios.get('/logs/'+page, {
					params: {
						type: 'dicing'
					}
				});
				if(res.data.error) {
					sendErrorNotification(res.data.error);
					return;
				}

				setLogs(res.data.logs);
				setPageTotal(res.data.pageTotal);
			} catch(err) {
				sendErrorNotification(err);
			}
		};

		loadPage();
	}, [ page ]);
	return (
		<>
			<TableSection
				info={info}
				actions={[]}
				headers={['Host', 'Player', 'Tile', 'Winner', 'Roll', 'Host Bet', 'Player Bet', 'Host IP', 'Player IP', 'Date']}
				rows={rows}
			/>
			<Pages
				pageTotal={pageTotal}
				base='/staff/logs/dicing'
			/>
		</>
	)
}
