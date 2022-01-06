import React from 'react';

import { formatNumber } from '../../../utils/format';
import DisplayUser from '../../utils/user/DisplayUser';

export default function HighscoreRow({ highscore, index }) {
    return (
        <tr key={index}>
            <td>{index + 1}</td>
            <td>{<DisplayUser user={highscore.user.username} rights={highscore.user.rights} />}</td>
            <td>{formatNumber(highscore.totalLevel)}</td>
            <td>{formatNumber(highscore.totalXP)}</td>
        </tr>
    )
}
