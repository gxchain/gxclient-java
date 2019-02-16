package com.gxchain.client.graphenej.models.contract;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 *
 */
@Data
public class ContractAccountProperties {
    private String id;
    @SerializedName("membership_expiration_date")
    private String membershipExpirationDate;
    private String registrar;
    private String referrer;
    @SerializedName("lifetime_referrer")
    private String lifetimeReferrer;
    @SerializedName("network_fee_percentage")
    private long networkFeePercentage;
    @SerializedName("lifetime_referrer_fee_percentage")
    private long lifetimeReferrerFeePercentage;
    @SerializedName("referrer_rewards_percentage")
    private long referrerRewardsPercentage;
    private String name;
    private String statistics;
    @SerializedName("whitelisting_accounts")
    private String[] whitelistingAccounts;
    @SerializedName("blacklisting_accounts")
    private String[] blacklistingAccounts;
    @SerializedName("whitelisted_accounts")
    private String[] whitelistedAccounts;
    @SerializedName("blacklisted_accounts")
    private String[] blacklistedAccounts;
    private Abi abi;
    @SerializedName("vm_type")
    private String vmType;
    @SerializedName("vm_version")
    private String vmVersion;

    private String code;
    @SerializedName("code_version")
    private String codeVersion;
}
