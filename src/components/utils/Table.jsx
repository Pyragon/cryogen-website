import React from 'react';

import { formatDate } from '../../utils/format';

import '../../styles/utils/Table.css';
import DisplayUser from './user/DisplayUser';

export default function Table({ headers, rows }) {
    if(!headers || !rows) return <></>;
    return (
        <div className='table-container'>
            <table>
                <thead>
                    <tr>
                        { headers.map((el, index) => <th key={index}>{el}</th>)}
                    </tr>
                </thead>
                <tbody>
                    { rows.map((row, index) => (
                        <tr key={index}>
                            { Object.values(row).map((el, index) => {
                                if(el.type === 'user')
                                    return (
                                        <td key={index}>
                                            <DisplayUser
                                                key={index}
                                                user={el.value}
                                            />
                                        </td>
                                    )
                                else if(el.type === 'date')
                                        return <td key={index}>{formatDate(el.value)}</td>
                                else
                                    return <td key={index}>{el.value}</td>;
                                
                            })}
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    )
}
