import React, { useContext, useState, useEffect } from 'react';
import axios from '../../utils/axios';

import PageContext from '../../utils/contexts/PageContext';
import NotificationContext from '../../utils/contexts/NotificationContext';

import TableSection from '../utils/sections/TableSection';
import Pages from '../utils/Pages';

const info = [

];

export default function CommandLogs() {
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
				value: log.extra.cmd[0].toLowerCase(),
			},
			{
				type: 'text',
				value: log.extra.cmd.splice(0, 1).join(', '),
			},
			{
				type: 'text',
				value: log.extra.cmd.join(' '),
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
				value: log.createdAt
			}
		];
	});

	useEffect(() => {

		let loadPage = async () => {

			try {
				let res = await axios.get('/logs/'+page, {
					params: {
						type: 'command'
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
				headers={['User', 'Command', 'Args', 'Full', 'Tile', 'IP', 'Date']}
				rows={rows}
			/>
			<Pages
				pageTotal={pageTotal}
				base='/staff/logs/command'
			/>
		</>
	)
}
