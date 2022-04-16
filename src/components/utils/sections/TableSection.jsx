import React, { useState } from 'react';
import IconInput from '../IconInput';
import Table from '../Table';
import Filters from './Filters';

export default function TableSection({ info, rows, actions, headers, allowSearch=true }) {
    let [ search, setSearch ] = useState('');
    let [ filters ] = useState([]);
    let infoStyle = {
        gridTemplateColumns: 'repeat('+info.length+', 1fr)'
    };
    return (
        <div>
            <div className='table-section-info' style={infoStyle}>
                { info.map((el, index) => <p key={index}>{el}</p>)}
            </div>
            <div className='table-section-actions'>
                { actions.map(action => (
                    <div key={action.title} onClick={() => action.onClick && action.onClick() } className='table-section-action'>
                        { action.icon && <i className={'fa '+action.icon} /> }
                        <span>{action.title}</span>
                    </div>
                ))}
            </div>
            { allowSearch && 
                <div className='table-section-search'>
                    <IconInput
                        icon='fa-search'
                        placeholder='Search Filters'
                        value={search}
                        setState={setSearch}
                    />
                </div>
            }
            { filters.length > 0 && <Filters filters={filters} /> }
            <div style={{padding: '10px'}}>
                <Table
                    headers={headers}
                    rows={rows}
                />
            </div>
        </div>
    )
}
