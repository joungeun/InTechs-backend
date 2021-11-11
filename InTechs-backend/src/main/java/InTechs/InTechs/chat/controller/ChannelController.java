package InTechs.InTechs.chat.controller;

import InTechs.InTechs.chat.payload.request.ChannelRequest;
import InTechs.InTechs.chat.service.ChannelService;
import InTechs.InTechs.user.payload.response.ProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/channel")
public class ChannelController {

    private final ChannelService channelService;

    @PostMapping("/{projectId}")
    public void createChannel(@PathVariable int projectId,
                              @RequestBody ChannelRequest channelRequest) {
        channelService.createChannel(projectId, channelRequest);
    }

    @PatchMapping("/{channelId}")
    public void updateChannelName(@PathVariable String channelId,
                                  @RequestBody ChannelRequest channelRequest) {

        channelService.updateChannel(channelId, channelRequest);
    }

    @DeleteMapping("/{channelId}")
    public void deleteChannel(@PathVariable String channelId) {
        channelService.deleteChannel(channelId);
    }

    @PostMapping("/{channelId}/{email}")
    public void addUser(@PathVariable String channelId,
                        @PathVariable String email) {
        channelService.AddUser(email, channelId);
    }

    @PatchMapping("/{channelId}/user")
    public void exitUser(@PathVariable String channelId) {
        channelService.exitChannel(channelId);
    }

    @GetMapping("/{channelId}")
    public List<ProfileResponse> getProfiles(@PathVariable  String channelId) {
        return channelService.getProfiles(channelId);
    }

}
