package nl.tudelft.sem.template.user.models;


public class UserApprovalModel {
    private transient long notificationId;
    private transient boolean approvalStatus;

    public UserApprovalModel(long notificationId, boolean approvalStatus) {
        this.notificationId = notificationId;
        this.approvalStatus = approvalStatus;
    }

    public long getNotificationId() {
        return notificationId;
    }

    public boolean isApprovalStatus() {
        return approvalStatus;
    }

}
