import React, { useState, useEffect } from 'react';

import HighscoreRow from './HighscoreRow';

import './../../../styles/index/MiniHighscores.css';

export default function MiniHighscores() {
    let [highscores, setHighscores] = useState([]);
    useEffect(() => {
        fetch('http://localhost:8081/highscores/mini')
            .then(res => res.json())
            .then(data => setHighscores(data.highscores));
    }, []);
    return (
        <div className="widget" style={{marginTop: '20px'}}>
            <div className="header">
                <h4>Highscores</h4>
            </div>
            <div className="content">
                <table className="table">
                    <thead>
                        <tr>
                            <th>Rank</th>
                            <th>Name</th>
                            <th>Total Level</th>
                            <th>Total XP</th>
                        </tr>
                    </thead>
                    <tbody>
                        {highscores.map((highscore, index) => 
                            <HighscoreRow key={index} highscore={highscore} index={index}/>
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    )
}
