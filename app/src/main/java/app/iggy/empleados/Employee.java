package app.iggy.empleados;

import java.io.Serializable;

class Employee implements Serializable {
    public static final long serialVersion = 20161120L;
    private long m_Id;
    private final String mName;
    private final String mDescription;
    private final int mSortOrder;
    private final String mMail;
    private final String mAddress;
    private final long mPhoneNumber;

    public Employee(long id, String name, String description, int sortOrder, String mail, String address, long phoneNumber) {
        this.m_Id = id;
        mName = name;
        mDescription = description;
        mSortOrder = sortOrder;
        mMail = mail;
        mAddress = address;
        mPhoneNumber = phoneNumber;
    }

    public long getId() {
        return m_Id;
    }

    public String getName() {
        return mName;
    }

    public String getDescription() {
        return mDescription;
    }

    public int getSortOrder() {
        return mSortOrder;
    }

    public String getMail() {
        return mMail;
    }

    public void setId(long id) {
        this.m_Id = id;
    }

    public String getAddress() {
        return mAddress;
    }

    public long getPhoneNumber() {
        return mPhoneNumber;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "m_Id=" + m_Id +
                ", mName='" + mName + '\'' +
                ", mDescription='" + mDescription + '\'' +
                ", mSortOrder='" + mSortOrder + '\'' +
                '}';
    }
}
