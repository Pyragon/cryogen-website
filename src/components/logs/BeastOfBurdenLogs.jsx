import React, { useContext, useEffect, useState } from 'react';
import axios from '../../utils/axios';

import NotificationContext from '../../utils/contexts/NotificationContext';

import PageContext from '../../utils/contexts/PageContext';

import TableSection from '../utils/sections/TableSection';
import Pages from '../utils/Pages';

const info = [

];

export default function BeastOfBurdenLogs() {
	let { page } = useContext(PageContext);
	
	let [ pageTotal, setPageTotal ] = useState(1);
	let [ logs, setLogs ] = useState([]);
	let { sendErrorNotification } = useContext(NotificationContext);

	
	let rows = logs.map(log => {
		return [
			{
				type: 'notify',
				value: false
			},
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
				type: 'text',
				value: log.items[0] ? log.items[0].defs.name+' x'+log.items[0].amount : 'N/A',
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
			let res = await axios.get('/logs/'+page, {
				params: {
					type: 'bob'
				}
			});
			if(res.data.error) {
				sendErrorNotification(res.data.error);
				return;
			}

			setLogs(res.data.logs);
			setPageTotal(res.data.pageTotal);
		};

		loadPage();
	}, [ page ]);
	return (
		<>
			<TableSection
				info={info}
				actions={[]}
				headers={[ 'User', 'Familiar', 'WorldTile', 'Item', 'IP', 'Date']}
				rows={rows}
			/>
			<Pages
				pageTotal={pageTotal}
				base='/staff/logs/beast_of_burden'
			/>
		</>
	)
}
