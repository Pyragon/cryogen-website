import React from 'react';

export default function FilterContainer({ children }) {
    return (
        <div className='filter-container'>
            { children }
            <i className='fa fa-cross' />
        </div>
    );
}
