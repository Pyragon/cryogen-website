import React, { useContext, useState, useEffect } from 'react';
import axios from '../../utils/axios';

import PageContext from '../../utils/contexts/PageContext';
import NotificationContext from '../../utils/contexts/NotificationContext';

import TableSection from '../utils/sections/TableSection';
import Pages from '../utils/Pages';

const info = [

];

function buildRulesText(rules) {
	return rules.map((rule, i) => indexedRules[i]+': '+rule).join('\n');
}

export default function DuellingLogs() {
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
				type: 'user',
				value: log.extra.challengerWon ? log.user : log.user2,
			},
			{
				type: 'items',
				value: log.items,
				short: true,
			},
			{
				type: 'items',
				value: log.items2,
				short: true
			},
			{
				type: 'text',
				value: buildRulesText(log.extra.rules),
			},
			{
				type: 'text',
				value: log.ip,
			},
			{
				type: 'text',
				value: log.extra.challengeeIp,
			}, 
			{
				type: 'date',
				value: log.date,
			}
		];
	});

	useEffect(() => {

		let loadPage = async () => {

			try {

				let res = await axios.get(`/logs/${page}?type=duelling`);

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
				headers={['Challenger', 'Challengee', 'Winner', 'Challenger Stake', 'Challengee Stake', 'Rules', 'Challenger IP', 'Challengee IP', 'Date']}
				rows={rows}
			/>
			<Pages
				pageTotal={pageTotal}
				base='/staff/logs/duelling'
			/>
		</>
	)
}

const NO_RANGED = 'No ranged', NO_MELEE = 'No melee', NO_MAGIC = 'No magic',
	NO_DRINKS = 'No drinks', NO_FOOD = 'No food', NO_PRAYER = 'No prayer',
	OBSTACLES = 'Obstacles', NO_FORFEIT = 'No forfeit', FUN_WEAPON = 'Fun weapon',
	NO_SPEC = 'No special attack', NO_HELM = 'No helm', NO_CAPE = 'No cape',
	NO_AMMY = 'No ammy', NO_WEAPON = 'No weapon', NO_BODY = 'No body',
	NO_SHIELD = 'No shield', NO_LEGS = 'No legs', NO_GLOVES = 'No gloves',
	NO_BOOTS = 'No boots', NO_RING = 'No ring', NO_ARROWS = 'No arrows',
	SUMMONING = 'Summoning', NO_MOVEMENT = 'No movement';

const indexedRules = [ NO_RANGED, NO_MELEE, NO_MAGIC, NO_DRINKS, NO_FOOD, 
	NO_PRAYER, OBSTACLES, NO_FORFEIT, FUN_WEAPON, NO_SPEC, NO_HELM, NO_CAPE, 
	NO_AMMY, NO_WEAPON, NO_BODY, NO_SHIELD, NO_LEGS, NO_GLOVES, NO_BOOTS, 
	NO_RING, NO_ARROWS, SUMMONING, NO_MOVEMENT ];
