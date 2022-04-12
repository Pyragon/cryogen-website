import React from 'react';

import { formatDate } from '../../utils/format';

import '../../styles/utils/Table.css';
import DisplayUser from './user/DisplayUser';

export default function Table({ headers, rows }) {
    if(!headers || !rows) return <></>;
    let notifyStyle = {
        backgroundColor: 'grey',
        color: 'black'
    };
    return (
        <div className='table-container'>
            <table>
                <thead>
                    <tr>
                        { headers.map((el, index) => <th key={index}>{el}</th>)}
                    </tr>
                </thead>
                <tbody>
                    { rows.map((row, index) => {
                        let shouldBeNotified = row.find(el => el.type === 'notify').value;
                        return (
                            <tr key={index} style={shouldBeNotified ? notifyStyle : {}}>
                                { Object.values(row).map((el, index) => {
                                    if(el.type === 'notify') return <></>;
                                    if(el.type === 'user')
                                        return (
                                            <td key={index}>
                                                <DisplayUser
                                                    key={index}
                                                    user={el.value}
                                                />
                                            </td>
                                        )
                                    else if(el.type === 'users')
                                            return (
                                                <td key={index}>
                                                    { el.value.map((user, index) => (
                                                        <DisplayUser
                                                            key={index}
                                                            user={user}
                                                            suffix={index === el.value.length-1 ? '' : ', '}
                                                        />
                                                    )) }
                                                </td>
                                            )
                                    else if(el.type === 'date')
                                            return <td key={index}>{formatDate(el.value)}</td>
                                    else if(el.type === 'button')
                                            return <td key={index} onClick={el.onClick}>{el.value}</td>
                                    else
                                        return <td key={index}>{el.value}</td>;
                                    
                                })}
                            </tr>
                        )
                    })}
                </tbody>
            </table>
        </div>
    )
}
