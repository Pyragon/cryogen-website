import React, { useState, useEffect } from 'react';

import HighscoreRow from './HighscoreRow';
import Widget from '../../utils/Widget';
import Table from '../../utils/Table';

import './../../../styles/index/MiniHighscores.css';

export default function MiniHighscores() {
    let [highscores, setHighscores] = useState([]);
    useEffect(() => {
        fetch('http://localhost:8081/highscores/mini')
            .then(res => res.json())
            .then(data => setHighscores(data.highscores));
    }, []);
    return (
        <Widget title="Highscores" style={{marginTop: '20px'}}>
            <Table headers={['Rank', 'Name', 'Total Level', 'Total XP']}>
                {highscores.map((highscore, index) => 
                    <HighscoreRow key={index} highscore={highscore} index={index}/>
                )}
            </Table>
        </Widget>
    )
}
