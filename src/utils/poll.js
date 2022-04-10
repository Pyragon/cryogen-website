import axios from './axios';

export default class Poll {

    constructor(data) {
        this._id = data._id;
        this.question = data.question;
        this.answers = data.answers;
        this.votes = data.votes;
        this.allowVoteChange = data.allowVoteChange;
        this.showResultsBeforeVote = data.showResultsBeforeVote;
    }

    hasVoted(user) {
        if (!user) return false;
        for (let i = 0; i < this.votes.length; i++)
            if (this.votes[i].user._id === user._id) return true;
        return false;
    }

    async vote(poll, index) {
        let res = await axios.post('/forums/threads/polls/vote', {
            poll: poll._id,
            index: index
        });
        if (res.data.error) {
            console.error('Error voting on poll', res.data.error);
            return;
        }
        return new Poll(res.data.poll);
    }

}