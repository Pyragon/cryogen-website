import React, { useContext, useState, useEffect } from 'react';
import axios from '../../utils/axios';

import PageContext from '../../utils/contexts/PageContext';
import NotificationContext from '../../utils/contexts/NotificationContext';

import TableSection from '../utils/sections/TableSection';
import Pages from '../utils/Pages';

const info = [

];

export default function DeathLogs() {
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
				type: 'tile',
				value: log.tile,
			},
			{
				type: log.extra.source.type === 'npc' ? 'text': 'user',
				value: log.extra.source.type === 'npc' ? log.extra.source.defs.name : log.extra.source.player,
			},
			{
				type: 'items',
				value: log.items
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
				let res = await axios.get('/logs/'+page, {
					params: {
						type: 'death'
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
				headers={['User', 'Tile', 'Source', 'Items Lost', 'IP', 'Date']}
				rows={rows}
			/>
			<Pages
				pageTotal={pageTotal}
				base='/staff/logs/death'
			/>
		</>
	)
}
