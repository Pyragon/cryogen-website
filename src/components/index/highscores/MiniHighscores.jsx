import React, { useState, useEffect } from 'react';
import axios from '../../../utils/axios';

import HighscoreRow from './HighscoreRow';
import Widget from '../../utils/Widget';
import Table from '../../utils/Table';

import './../../../styles/index/MiniHighscores.css';

export default function MiniHighscores() {
    let [highscores, setHighscores] = useState([]);
    useEffect(() => {
        
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
