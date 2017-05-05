package com.cryo.modules.staff.search.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.cryo.modules.account.support.punish.PunishDAO;
import com.cryo.modules.staff.search.Filter;

import lombok.*;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: May 03, 2017 at 9:04:52 PM
 */
public class StatusFilter extends Filter {

	public StatusFilter() {
		super("status");
	}

	@Override
	public String getFilter() {
		return null;
	}
	
	public List<PunishDAO> filterList(List<PunishDAO> list) {
		return list.stream()
				.filter(this::hasDesiredStatus)
				.collect(Collectors.toList());
	}
	
	public boolean hasDesiredStatus(PunishDAO punish) {
		int intVal = (this.value instanceof Integer) ? (int) this.value : 0;
		if(punish.getAppeal() == null && intVal != 0)
			return intVal == -1;
		return punish.getAppeal().getActive() == intVal;
	}

	@Override
	public boolean setValue(String value) {
		value = value.toLowerCase();
		if(!value.equals("declined") && !value.equals("accepted") && !value.equals("pending") && !value.equals("none"))
			return false;
		this.value = value.equals("declined") ? 2 : value.equals("accepted") ? 1 : value.equals("none") ? -1 : 0;
		return true;
	}
	
}
