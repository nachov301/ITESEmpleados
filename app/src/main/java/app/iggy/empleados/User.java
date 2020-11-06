package app.iggy.empleados;

import java.io.Serializable;

class User implements Serializable {
    public static final long serialVersion = 20171120L;
    private long m_Id;
    private final String mName;
    private final String mPassword;

    public User(long m_Id, String name, String password) {
        this.m_Id = m_Id;
        mName = name;
        mPassword = password;
    }

    public long getM_Id() {
        return m_Id;
    }

    public void setM_Id(long m_Id) {
        this.m_Id = m_Id;
    }

    public String getName() {
        return mName;
    }

    public String getPassword() {
        return mPassword;
    }

    @Override
    public String toString() {
        return "User{" +
                "m_Id=" + m_Id +
                ", mName='" + mName + '\'' +
                ", mPassword='" + mPassword + '\'' +
                '}';
    }
}
