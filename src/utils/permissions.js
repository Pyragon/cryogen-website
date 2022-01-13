export default class Permissions {

    constructor(permissions) {
        this.permissions = permissions;
    }

    canSee(user, thread) {
        let data = this.permissions.canSee;
        if (data.includes['-1']) return true;
        if (data.includes['-2'] && user !== null) return true;
        if (user === null) return false;
        for (let id of data)
            if (id === user.displayGroup._id) return true;
        for (let i = 0; i < user.userGroups.length; i++)
            if (data.includes(user.userGroups[i])) return true;
        if (data.includes['-3'] && user !== null && thread && thread.author._id === user._id) return true;
        if (data.includes['-4'] && user !== null && thread && thread.author.displayGroup.rights > 0) return true;
        return false;
    }

    canModerate(user) {
        if (!user) return false;
        let data = this.permissions.canModerate
        for (let id of data)
            if (id === user.displayGroup._id) return true;
        if (user.userGroups)
            for (let i = 0; i < user.userGroups.length; i++)
                if (data.includes(user.userGroups[i])) return true;
        return false;
    }

}