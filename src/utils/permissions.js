import constants from "./constants";

export default class Permissions {

    constructor(permissions) {
        this.permissions = permissions;
    }

    canSee(user, thread) {
        let data = this.permissions.canSee;
        if (data.includes('-1')) return true;
        if (data.includes('-2') && user !== null) return true;
        if (user === null) return false;
        for (let id of data)
            if (id === user.displayGroup._id) return true;
        for (let i = 0; i < user.usergroups.length; i++)
            if (data.includes(user.usergroups[i]._id)) return true;
        if (data.includes('-3') && user !== null && thread && thread.author._id === user._id) return true;
        if (data.includes('-4') && user !== null && thread && thread.author.displayGroup.rights > 0) return true;
        return false;
    }

    canEdit(user, post) {
        if (!user || !post) return false;
        let data = this.permissions.canEdit;
        if (data.includes('-3') && post.author._id === user._id) return true;
        for (let id of data)
            if (id === user.displayGroup._id) return true;
        for (let i = 0; i < user.usergroups.length; i++)
            if (data.includes(user.usergroups[i]._id)) return true;
        return false;
    }

    canModerate(user) {
        if (!user) return false;
        let data = this.permissions.canModerate
        for (let id of data)
            if (id === user.displayGroup._id) return true;
        if (user.usergroups)
            for (let i = 0; i < user.usergroups.length; i++)
                if (data.includes(user.usergroups[i]._id)) return true;
        return false;
    }

    canReply(user, thread) {
        if (!user) return false;
        if (user.displayGroup._id === constants.BANNED_USERGROUP) return false;
        let data = this.permissions.canReply;
        if (data.includes('-1') || data.includes('-2')) return true;
        for (let id of data)
            if (id === user.displayGroup._id) return true;
        if (user.usergroups)
            for (let i = 0; i < user.usergroups.length; i++)
                if (data.includes(user.usergroups[i]._id)) return true;
        if (data.includes('-3') && thread.author._id === user._id) return true;
        return false;
    }

    canCreateThreads(user) {
        if (!user) return false;
        let data = this.permissions.canCreateThreads;
        console.log('Checking if user can create threads');
        console.log(user, data);
        if (data.includes('-1') || data.includes('-2')) return true;
        for (let id of data)
            if (id === user.displayGroup._id) return true;
        if (user.usergroups)
            for (let i = 0; i < user.usergroups.length; i++)
                if (data.includes(user.usergroups[i]._id)) return true;
        return false;
    }

    canCreatePolls(user) {
        if (!user) return false;
        let data = this.permissions.canCreatePolls;
        if (data.includes('-1') || data.includes('-2')) return true;
        for (let id of data)
            if (id === user.displayGroup._id) return true;
        if (user.usergroups)
            for (let i = 0; i < user.usergroups.length; i++)
                if (data.includes(user.usergroups[i]._id)) return true;
        return false;
    }

}