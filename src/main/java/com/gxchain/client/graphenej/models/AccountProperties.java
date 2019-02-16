package com.gxchain.client.graphenej.models;

import com.gxchain.client.graphenej.models.contract.Abi;
import com.gxchain.client.graphenej.objects.AccountOptions;
import com.gxchain.client.graphenej.objects.Authority;
import lombok.Data;

/**
 * Created by nelson on 11/15/16.
 *
 * Details of Dynamic Account specs can be found at
 * https://bitshares.org/technology/dynamic-account-permissions/
 *
 */
@Data
public class AccountProperties {
    private String id;
    private String membership_expiration_date;
    private String registrar;
    private String referrer;
    private String lifetime_referrer;
    private long network_fee_percentage;
    private long lifetime_referrer_fee_percentage;
    private long referrer_rewards_percentage;
    private String name;
    private Authority owner;
    private Authority active;
    private AccountOptions options;
    private String statistics;
    private String[] whitelisting_accounts;
    private String[] blacklisting_accounts;
    private String[] whitelisted_accounts;
    private String[] blacklisted_accounts;
    private Object[] owner_special_authority;
    private Object[] active_special_authority;
    private long top_n_control_flags;
    private Abi abi;

    /**
     * active public key
     * @return
     */
    public String getActivePublicKey(){
        return getActive().getKeyAuthList().get(0).getAddress();
    }

    /**
     * memo public key
     * @return
     */
    public String getMemoPublicKey(){
        return getOptions().getMemoKey().getAddress();
    }
}
