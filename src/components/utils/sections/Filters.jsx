import React from 'react';
import UserFilter from './filters/UserFilter';

export default function Filters({ filters }) {
    return (
        <div className='table-section-filters'>
            {filters.map(filter => {
                if(filter.type === 'user')
                    return <UserFilter filter={filter} />
                return <></>
            })}
        </div>
    );
}
