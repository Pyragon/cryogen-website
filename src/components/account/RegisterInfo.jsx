import React from 'react'

export default function RegisterInfo() {
    return (
        <div className='register-info'>
            <i className='fa fa-user-plus' />
            <h3 className='t-center'>Registration Info:</h3>
            <ul style={{fontSize: '.85rem'}}>
                <li>Usernames: Must be between 3 and 12 alphanumeric characters in length. Donators+ have the ability to change this name.</li>
                <li>Passwords: Must be between 8 and 50 characters and can contain any combination of symbols, numbers, and alpha characters. Passwords are case sensitive.</li>
                <li>Emails and Discord accounts may be linked through the account section once registered.</li>
            </ul>
        </div>
    )
}
