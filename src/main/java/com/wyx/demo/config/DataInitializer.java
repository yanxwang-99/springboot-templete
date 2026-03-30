package com.wyx.demo.config;

import com.wyx.demo.entity.Menu;
import com.wyx.demo.entity.User;
import com.wyx.demo.entity.UserCode;
import com.wyx.demo.repository.MenuRepository;
import com.wyx.demo.repository.UserCodeRepository;
import com.wyx.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserCodeRepository userCodeRepository;
    private final MenuRepository menuRepository;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            initUsers();
            initCodes();
            initMenus();
        }
    }

    private void initUsers() {
        userRepository.saveAll(List.of(
                User.builder()
                        .username("vben")
                        .password("123456")
                        .realName("Vben")
                        .roles(List.of("super"))
                        .homePath(null)
                        .build(),
                User.builder()
                        .username("admin")
                        .password("123456")
                        .realName("Admin")
                        .roles(List.of("admin"))
                        .homePath("/workspace")
                        .build(),
                User.builder()
                        .username("jack")
                        .password("123456")
                        .realName("Jack")
                        .roles(List.of("user"))
                        .homePath("/workspace")
                        .build()
        ));
    }

    private void initCodes() {
        userCodeRepository.saveAll(List.of(
                // vben (super) codes
                UserCode.builder().username("vben").code("AC_100100").build(),
                UserCode.builder().username("vben").code("AC_100110").build(),
                UserCode.builder().username("vben").code("AC_100120").build(),
                UserCode.builder().username("vben").code("AC_100010").build(),
                // admin codes
                UserCode.builder().username("admin").code("AC_100010").build(),
                UserCode.builder().username("admin").code("AC_100020").build(),
                UserCode.builder().username("admin").code("AC_100030").build(),
                // jack (user) codes
                UserCode.builder().username("jack").code("AC_1000001").build(),
                UserCode.builder().username("jack").code("AC_1000002").build()
        ));
    }

    private void initMenus() {
        for (String username : List.of("vben", "admin", "jack")) {
            String role = switch (username) {
                case "vben" -> "super";
                case "admin" -> "admin";
                default -> "user";
            };
            createMenusForUser(username, role);
        }
    }

    private void createMenusForUser(String username, String role) {
        // Dashboard menu
        Menu analytics = Menu.builder()
                .username(username)
                .name("Analytics")
                .path("/analytics")
                .component("/dashboard/analytics/index")
                .title("page.dashboard.analytics")
                .affixTab(true)
                .sortOrder(0)
                .build();

        Menu workspace = Menu.builder()
                .username(username)
                .name("Workspace")
                .path("/workspace")
                .component("/dashboard/workspace/index")
                .title("page.dashboard.workspace")
                .sortOrder(1)
                .build();

        Menu dashboard = Menu.builder()
                .username(username)
                .name("Dashboard")
                .path("/dashboard")
                .redirect("/analytics")
                .title("page.dashboard.title")
                .sortOrder(-1)
                .children(List.of(analytics, workspace))
                .build();

        analytics.setParent(dashboard);
        workspace.setParent(dashboard);

        menuRepository.save(dashboard);

        // Demos menu
        createDemosMenu(username, role);
    }

    private void createDemosMenu(String username, String role) {
        Menu pageControl = Menu.builder()
                .username(username)
                .name("AccessPageControlDemo")
                .path("/demos/access/page-control")
                .component("/demos/access/index")
                .title("demos.access.pageAccess")
                .icon("mdi:page-previous-outline")
                .sortOrder(0)
                .build();

        Menu buttonControl = Menu.builder()
                .username(username)
                .name("AccessButtonControlDemo")
                .path("/demos/access/button-control")
                .component("/demos/access/button-control")
                .title("demos.access.buttonControl")
                .icon("mdi:button-cursor")
                .sortOrder(1)
                .build();

        Menu roleMenu = createRoleSpecificMenu(username, role);

        Menu accessDemos = Menu.builder()
                .username(username)
                .name("AccessDemos")
                .path("/demosaccess")
                .title("demos.access.backendPermissions")
                .icon("mdi:cloud-key-outline")
                .sortOrder(0)
                .children(List.of(pageControl, buttonControl, roleMenu))
                .build();

        pageControl.setParent(accessDemos);
        buttonControl.setParent(accessDemos);
        roleMenu.setParent(accessDemos);

        Menu demos = Menu.builder()
                .username(username)
                .name("Demos")
                .path("/demos")
                .redirect("/demos/access")
                .title("demos.title")
                .icon("ic:baseline-view-in-ar")
                .sortOrder(1000)
                .children(List.of(accessDemos))
                .build();

        accessDemos.setParent(demos);

        menuRepository.save(demos);
    }

    private Menu createRoleSpecificMenu(String username, String role) {
        return switch (role) {
            case "super" -> Menu.builder()
                    .username(username)
                    .name("AccessSuperVisibleDemo")
                    .path("/demos/access/super-visible")
                    .component("/demos/access/super-visible")
                    .title("demos.access.superVisible")
                    .icon("mdi:button-cursor")
                    .sortOrder(2)
                    .build();
            case "admin" -> Menu.builder()
                    .username(username)
                    .name("AccessAdminVisibleDemo")
                    .path("/demos/access/admin-visible")
                    .component("/demos/access/admin-visible")
                    .title("demos.access.adminVisible")
                    .icon("mdi:button-cursor")
                    .sortOrder(2)
                    .build();
            default -> Menu.builder()
                    .username(username)
                    .name("AccessUserVisibleDemo")
                    .path("/demos/access/user-visible")
                    .component("/demos/access/user-visible")
                    .title("demos.access.userVisible")
                    .icon("mdi:button-cursor")
                    .sortOrder(2)
                    .build();
        };
    }
}
