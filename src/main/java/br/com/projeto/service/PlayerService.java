package br.com.projeto.service;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import br.com.projeto.dao.PlayerDao;
import br.com.projeto.entity.Player;
import br.com.projeto.util.CryptUtils;

@Service
public class PlayerService {

	@Autowired
	private PlayerDao dao;
	
	public Player login(String username, String password) {
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpSession session = attr.getRequest().getSession(true);
		
		Player player = dao.login(username, CryptUtils.md5(password));
		
		// nao conseguiu autenticar
		if (player == null) {
			session.setAttribute("LoginMessage", "Usu�rio ou senha inv�lido.");
			return null;
		}
		
		session.setAttribute("Player", player);
		
		return player;
	}
	
	
	public void logout() {
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpSession session = attr.getRequest().getSession(true);
		session.removeAttribute("Player");
	}

	
	public boolean newPlayer() {
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpSession session = attr.getRequest().getSession(true);
		
		Player player = new Player();
		
		player.setUsername(attr.getRequest().getParameter("username"));
		player.setPassword( CryptUtils.md5(attr.getRequest().getParameter("password")) );
		player.setXp(0);
		player.setEnergy(100);

		boolean result = dao.insert(player);
		
		if (result) {
			session.setAttribute("LoginMessage", "Cadastro realizado com sucesso.");
		} else {
			session.setAttribute("CadastroMessage", "Preencha corretamente o cadastro.");
		}
		
		return result;
	}
	
	public boolean hasOpenCase() {
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpSession session = attr.getRequest().getSession(true);

		Player player = (Player) session.getAttribute("Player");
		
		if (player == null) return false;
		
		return (player.getCaseOpen() != null);
	}
	
	public void openCase() {
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpSession session = attr.getRequest().getSession(true);

		Player player = (Player) session.getAttribute("Player");
		
		if (player == null) return;
		
		player.setCaseOpen(Integer.parseInt(attr.getRequest().getParameter("caseId")));
		
		dao.update(player);
	}


}
