package stg.onyou.model;

import lombok.Getter;

@Getter
public enum Role {
    MEMBER(false, false, false, false, false, false, false, false),
    MANAGER(false, false, false, false, true, true, true, true),
    MASTER(true, true, true, true, true, true, true, true)

    Role(boolean canAllocateRole, boolean canWithdrawManager, boolean canDeleteFeed, boolean canDeleteClub,
         boolean canEditClub, boolean canWithdrawMember, boolean canManipulateClubSchedule, boolean canApproveApply){

        this.canAllocateRole = canAllocateRole;
        this.canWithdrawManager = canWithdrawManager;
        this.canDeleteFeed = canDeleteFeed;
        this.canDeleteClub = canDeleteClub;
        this.canEditClub = canEditClub;
        this.canWithdrawMember = canWithdrawMember;
        this.canManipulateClubSchedule = canManipulateClubSchedule;
        this.canApproveApply = canApproveApply;

    }

    private final boolean canAllocateRole; // MASTER
    private final boolean canWithdrawManager; // MASTER
    private final boolean canDeleteFeed; // MASTER
    private final boolean canDeleteClub; // MASTER

    private final boolean canEditClub; // MANAGER
    private final boolean canWithdrawMember; // MANAGER
    private final boolean canManipulateClubSchedule; // MANAGER. manipulate : Create, Update, Delete
    private final boolean canApproveApply; // MANAGER

    }
