package com.opteral.gateway.validation;

import com.opteral.gateway.Config;
import com.opteral.gateway.GatewayServletListener;
import com.opteral.gateway.GatewayException;
import com.opteral.gateway.json.JSON_SMS;

import java.util.Date;
import java.util.List;

public class CheckerSMS {

    private Validator validator;

    public CheckerSMS(Validator validator) {
        this.validator = validator;
    }

    public void check(List<JSON_SMS> listaJSON_SMS) throws GatewayException {

        for (JSON_SMS sms : listaJSON_SMS )
        {
            check(sms);
        }

    }

    public void check(JSON_SMS jsonsms) throws GatewayException {

        if (!isValidTexto(jsonsms))
            throw new GatewayException("Error: Valid text is needed on sms: "+jsonsms.getSubid());
        if (!isValidSender(jsonsms))
            throw new GatewayException("Error: Valid sender is needed  on sms: "+jsonsms.getSubid());
        if (!jsonsms.isForDelete() && !validator.isMsisdn(jsonsms.getMsisdn()))
            throw new GatewayException("Error: Valid msisdn is needed  on sms: "+jsonsms.getSubid());
        if (!isValidAck(jsonsms))
            throw new GatewayException("Error: ACK configuration issue  on sms: "+jsonsms.getSubid());
        if (!isValidSubid(jsonsms.getSubid()))
            throw new GatewayException("Error: subid size must be lower than "+ Config.SUBID_MAX_SIZE+ " on sms: "+jsonsms.getSubid());
        if (!isValidDateTime(jsonsms.getDatetime()))
            throw new GatewayException("Error:  The scheduled date can not be earlier than the current date, on sms: "+jsonsms.getSubid());


    }

    private boolean isValidDateTime(Date datetime)
    {
        if (datetime == null)
            return true;
        else
        {

            if (datetime.before(new Date()))
                return false;
            else
                return true;
        }
    }

    private boolean isValidSubid(String subid)
    {
        if (subid == null)
            return true;
        else
        {
            if (subid.length() > Config.SUBID_MAX_SIZE)
                return false;
            else
                return true;
        }
    }

    private boolean isValidTexto(JSON_SMS jsonsms)
    {
        if (jsonsms.isForDelete())
            return true;

        String texto = jsonsms.getText();

        return !(texto == null || texto.isEmpty() || texto.length() > Config.MAX_SMS_SIZE);


    }

    private boolean isValidSender(JSON_SMS json_sms)
    {
        if (json_sms.isForDelete())
            return true;
        if (json_sms.getSender() == null)
            return false;

        return (json_sms.getSender().length() > 0 && json_sms.getSender().length() < Config.SENDER_MAX_SIZE);

    }

    private boolean isValidAck(JSON_SMS jsonsms)
    {
        if (jsonsms.getAck_url() == null)
            return true;

        if (jsonsms.getAck_url().isEmpty())
        {
            return true;
        }
        else
        {
            if (jsonsms.getSubid() == null || jsonsms.getSubid().isEmpty())
            {
                return false;
            }
            else
            {
                try
                {
                    return validator.isURL(jsonsms.getAck_url());
                }
                catch (GatewayException e) {
                    return false;
                }


            }
        }

    }




}
