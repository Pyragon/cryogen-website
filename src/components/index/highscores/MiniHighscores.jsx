import React, { useState, useEffect } from 'react';
import axios from '../../../utils/axios';

import Widget from '../../utils/Widget';
import Table from '../../utils/Table';

import { formatNumber } from '../../../utils/format';

import './../../../styles/index/MiniHighscores.css';

export default function MiniHighscores() {
    let [ highscores, setHighscores ] = useState([]);
    useEffect(() => {
        
        let loadMini = async () => {

            let res = await axios.get('/highscores/mini');
            setHighscores(res.data.highscores);
            console.log(res.data.highscores);

        };

        loadMini();

    }, []);

    let rows = highscores.map((highscore, index) => {
        return [
            {
                type: 'text',
                value: index + 1,
            },
            {
                type: 'user',
                value: highscore.user,
            },
            {
                type: 'number',
                value: formatNumber(highscore.totalLevel),
            },
            {
                type: 'number',
                value: formatNumber(highscore.totalXP),
            }
        ]
    });
    return (
        <Widget title="Highscores" style={{marginTop: '20px'}}>
            <Table headers={['Rank', 'Name', 'Total Level', 'Total XP']} rows={rows} />
        </Widget>
    )
}
