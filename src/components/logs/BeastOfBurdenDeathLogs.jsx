import React, { useContext, useEffect, useState } from 'react';
import axios from '../../utils/axios';
import NotificationContext from '../../utils/contexts/NotificationContext';

import PageContext from '../../utils/contexts/PageContext';
import Pages from '../utils/Pages';

import TableSection from '../utils/sections/TableSection';

const info = [

];

export default function BeastOfBurdenDeathLogs() {
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
				value: log.npc.name,
			},
			{
				type: 'tile',
				value: log.tile,
			},
			{
				type: 'items',
				value: log.items,
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

				let res = await axios.get(`/logs/${page}?type=bob_death`);
	
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
				headers={[ 'User', 'Familiar', 'WorldTile', 'Items', 'IP', 'Date']}
				rows={rows}
			/>
			<Pages
				pageTotal={pageTotal}
				base='/staff/logs/beast_of_burden_death'
			/>
		</>
	)
}
