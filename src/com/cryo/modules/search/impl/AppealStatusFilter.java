package com.cryo.modules.search.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.cryo.modules.account.entities.Punishment;
import com.cryo.modules.search.Filter;

import lombok.*;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: May 03, 2017 at 9:04:52 PM
 */
public class AppealStatusFilter extends Filter {

	public AppealStatusFilter() {
		super("appeal-status");
	}

	@Override
	public boolean setValue(String mod, String value) {
		value = value.toLowerCase();
		if(!value.equals("declined") && !value.equals("accepted") && !value.equals("pending") && !value.equals("none"))
			return false;
		int status = value.equals("declined") ? 2 : value.equals("accepted") ? 1 : value.equals("none") ? -1 : 0;
		if(mod.equals("appeals") && status == -1)
			return false;
		this.value = status;
		return true;
	}

	@Override
	public String getFilter(String mod) {
		if(mod.equals("punishments") || !(value instanceof Integer) || (int) value == -1)
			return null;
		return "active=?";
	}
	
	@SuppressWarnings("unchecked")
	public List<?> filterList(List<?> list) {
		if(list.size() == 0) return list;
		if(!(list.get(0) instanceof Punishment))
			return list;
		List<Punishment> punishments = (List<Punishment>) list;
		return punishments.stream()
				.filter(this::hasDesiredStatus)
				.collect(Collectors.toList());
	}
	
	public boolean hasDesiredStatus(Punishment punish) {
		int intVal = (this.value instanceof Integer) ? (int) this.value : 0;
		if(punish.getAppeal() == null && intVal != 0)
			return intVal == -1;
		return punish.getAppeal().getActive() == intVal;
	}

	@Override
	public boolean appliesTo(String mod, boolean archived) {
		return isMod(mod, "punishments", "staff-punishments");
	}
	
}
