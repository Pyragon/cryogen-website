import React from 'react';

import { Link } from 'react-router-dom';

import { formatDate, formatGp, formatNumber } from '../../utils/format';

import '../../styles/utils/Table.css';
import DisplayUser from './user/DisplayUser';
import TileImage from './table/TileImage';
import LongText from './table/LongText';
import ItemsContainer from './table/ItemsContainer';
import UserGroupsHover from './table/UserGroupsHover';
import Hoverable from './table/Hoverable';

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
                        let notifyEl = row.find(el => el.type === 'notify');
                        let shouldBeNotified = notifyEl && notifyEl.value;
                        return (
                            <tr key={index} style={shouldBeNotified ? notifyStyle : {}}>
                                { Object.values(row).map((el, index) => {
                                    if(el.type === 'notify') return null;
                                    if(el.type === 'user')
                                        return (
                                            <td key={index}>
                                                <DisplayUser
                                                    key={index}
                                                    user={el.value}
                                                />
                                            </td>
                                        )
                                    if(el.type === 'users')
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
                                    if(el.type === 'date')
                                        return <td key={index} style={el.style} className={el.className}>{formatDate(el.value)}</td>
                                    if(el.type === 'button')
                                        return <td key={index} style={el.style} className={el.className} onClick={el.onClick}>{el.value}</td>
                                    if(el.type === 'link')
                                        return <td key={index} style={el.style} className={el.className} onClick={el.onClick}><Link className='link' to={el.link}>{el.value}</Link></td>
                                    if(el.type === 'tile')
                                        return <TileImage key={index} tile={el.value} />
                                    if(el.type === 'gp')
                                        return <td key={index} style={el.style} className={el.className} title={formatNumber(el.value)}>{formatGp(el.value)}</td>
                                    if(el.type === 'items')
                                        return <ItemsContainer key={index} items={el.value} short={el.short} />
                                    if(el.type === 'element')
                                        return (
                                            <td key={index} style={el.style} className={el.className}>
                                                { typeof el.value === 'function' ? el.value() : el.value }
                                            </td>
                                        )
                                    if(el.type === 'select')
                                        return (
                                            <td key={index} style={el.style} className={el.className}>
                                                <select className='input' ref={el.ref}>
                                                    { Object.keys(el.value).map((key, index) => (
                                                        <option key={index} value={key}>{el.value[key]}</option>
                                                    ))}
                                                </select>
                                            </td>
                                        )
                                    if(el.type === 'multiselect')
                                        return (
                                            <td key={index} style={el.style} className={el.className}>
                                                <p>Test</p>
                                            </td>
                                        )
                                    if(el.type === 'groups')
                                        return (
                                            <td key={index} style={el.style} className={el.className}>
                                                <UserGroupsHover allowed={el.value} groups={el.groups} />
                                            </td>
                                        )
                                    if(el.type === 'hover')
                                        return (
                                            <td key={index}>
                                                <Hoverable shortTitle={el.shortTitle} values={el.value} />
                                            </td>
                                        )
                                    if(!el.noLongText && el.value.length > 20)
                                        return <LongText style={el.style} className={el.className} key={index} text={el.value} />
                                    return <td style={el.style} className={el.className} key={index}>{el.value}</td>;
                                })}
                            </tr>
                        )
                    })}
                </tbody>
            </table>
        </div>
    )
}
