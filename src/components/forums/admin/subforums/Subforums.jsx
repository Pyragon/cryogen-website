import React from 'react'

export default function Subforums() {
    return (
        <>
            <div className='table-section-info' style={{
                gridTemplateColumns: 'repeat(2, 1fr)'
            }}>
                <p>Subforum create/edit info</p>
                <p>Subforum create/edit info</p>
            </div>
            <div className='table-section-actions'>
                <div className='table-section-action'>
                    <i className='fa fa-plus-circle' />
                    <span>Create Subforum</span>
                </div>
            </div>
        </>
    )
}
