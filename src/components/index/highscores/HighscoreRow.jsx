import React from 'react';

import { crownUser, formatNumber } from '../../../utils/format';

export default function HighscoreRow({ highscore, index }) {
    return (
        <tr key={index}>
            <td>{index + 1}</td>
            <td>{crownUser(highscore.username)}</td>
            <td>{formatNumber(highscore.totalLevel)}</td>
            <td>{formatNumber(highscore.totalXP)}</td>
        </tr>
    )
}
