package stg.onyou.model;

import lombok.Getter;

@Getter
public enum Role {
    PENDING(false, false, false, false, false, false, false),
    MEMBER(false, false, false, false, false, false, false),
    MANAGER(false, false, true, true, true, true, true),
    MASTER(true, true, true, true, true, true, true );

    Role(boolean canAllocateRole, boolean canDeleteClub, boolean canDeleteFeed,
         boolean canEditClub,  boolean canManipulateClubSchedule, boolean canApproveApply, boolean canRejectApply){

        this.canAllocateRole = canAllocateRole;
        this.canDeleteClub = canDeleteClub;
        this.canDeleteFeed = canDeleteFeed;
        this.canEditClub = canEditClub;
        this.canManipulateClubSchedule = canManipulateClubSchedule;
        this.canApproveApply = canApproveApply;
        this.canRejectApply = canRejectApply;

    }

    private final boolean canAllocateRole; // MASTER
    private final boolean canDeleteClub; // MASTER
    private final boolean canDeleteFeed; // MANAGER
    private final boolean canEditClub; // MANAGER
    private final boolean canManipulateClubSchedule; // MANAGER
    private final boolean canApproveApply; // MANAGER
    private final boolean canRejectApply; // MANAGER

    }
