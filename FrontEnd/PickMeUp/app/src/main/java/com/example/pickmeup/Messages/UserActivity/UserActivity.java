package com.example.pickmeup.Messages.UserActivity;

public class UserActivity {
    private String name;

    /**
     * @param name sets the name of the user
     */
    public UserActivity(String name) {
        this.name = name;
    }

    /**
     * @return name of user
     */
    public String getName() {
        return name;
    }

    /**
     * @param o name of user
     * @return true if the param is equal to the user, otherwise false
     */
    @Override
    public boolean equals(Object o){
        if(o == null) return false;

        return name.equals(((UserActivity) o).getName());
    }

    /**
     * @return
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
