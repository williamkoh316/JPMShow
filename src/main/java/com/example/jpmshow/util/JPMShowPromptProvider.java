package com.example.jpmshow.util;

import org.jline.utils.AttributedString;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

import com.example.jpmshow.service.LoginServiceImpl;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JPMShowPromptProvider implements PromptProvider {
	private final LoginServiceImpl login;

	@Override
	public AttributedString getPrompt() {
		String msg = String.format("JPMShow (%s)>", login.isLogin() ? login.getUser().name().toLowerCase() : "logout");
		return new AttributedString(msg);
	}


}
