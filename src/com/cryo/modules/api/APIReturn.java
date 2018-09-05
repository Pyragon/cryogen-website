package com.cryo.modules.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
public class APIReturn {

    private final @Getter String name, type, description;

    private @Getter int rights;
}
