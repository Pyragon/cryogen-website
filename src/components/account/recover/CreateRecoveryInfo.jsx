import React from 'react'

export default function CreateRecoveryInfo() {
    return (
        <div className='create-recovery-info'>
            <span>
                <ul style={{fontSize: '.85rem'}}>
                    <li>Information on this page is checked against your account and manually reviewed by Admins that will then determine whether or not to approve your recovery.</li>
                    <li>If you have an email or discord account linked to your account, and you enter either one here, you will receive a message via those platforms to recover your account faster.</li>
                    <li>Personal information like your passwords, emails, recovery questions/answers, etc. are not shown directly to the Admins</li>
                    <li>The only information saved/shown is whether or not you got this questions correct based on your account.</li>
                    <li>Please view this forum page if you have any other questions on how this system works.</li>
                </ul>
            </span>
        </div>
    )
}
