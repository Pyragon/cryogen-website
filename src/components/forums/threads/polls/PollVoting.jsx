import React from 'react'
import Button from '../../../utils/Button';

export default function PollVoting({ poll, voteOnOption, setShowResults }) {
    return (
        <>
            { poll.answers.map((answer, index) => {
                return (
                    <div style={{
                        display: 'grid',
                        gridTemplateColumns: '1fr 1fr',
                        height: '30px'
                    }}>
                    <p style={{textAlign: 'center' }}>{answer}</p>
                    <Button style={{marginRight: '10px', marginTop: '10px'}}onClick={() => voteOnOption(index)}>Vote</Button>
                    </div>
                )
            }) }
            { poll.showResultsBeforeVote && 
                <div className="poll-config-container">
                    <div onClick={() => setShowResults(true)} className="poll-change-vote">View Results</div>
                </div>
            }
        </>
    )

}
