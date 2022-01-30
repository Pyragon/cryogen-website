import React from 'react';

import '../../styles/utils/Table.css';

export default function Table({ headers, rows }) {
    if(!headers || !rows) return <></>;
    return (
        <table>
            <thead>
                { headers.map((el, index) => <th key={index}>{el}</th>)}
            </thead>
            <tbody>
                { rows.map((row, index) => (
                    <tr key={index}>
                        { row.map((el, index) => <td key={index}>{el}</td>)}
                    </tr>
                ))}
            </tbody>
        </table>
    )
}
