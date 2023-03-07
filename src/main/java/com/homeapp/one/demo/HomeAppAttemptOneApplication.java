package com.homeapp.one.demo;

import com.homeapp.one.demo.models.bike.Frame;
import com.homeapp.one.demo.models.bike.FrontGears;
import com.homeapp.one.demo.models.bike.FullBike;
import com.homeapp.one.demo.models.bike.RearGears;
import com.homeapp.one.demo.services.FullBikeService;
import com.homeapp.one.demo.services.StickyNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static com.homeapp.one.demo.models.bike.Enums.BrakeType.*;
import static com.homeapp.one.demo.models.bike.Enums.FrameStyle.*;
import static com.homeapp.one.demo.models.bike.Enums.GroupsetBrand.*;
import static com.homeapp.one.demo.models.bike.Enums.HandleBarType.*;
import static com.homeapp.one.demo.models.bike.Enums.ShifterStyle.*;
import static com.homeapp.one.demo.models.bike.Enums.SramGroupSet.*;
import static com.homeapp.one.demo.models.bike.Enums.ShimanoGroupSet.*;

@SpringBootApplication
public class HomeAppAttemptOneApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(HomeAppAttemptOneApplication.class, args);
	}

	@Autowired
	private StickyNoteService stickyNoteService;
	@Autowired
	private FullBikeService fullBikeService;

	@Override
	public void run(String... args) throws Exception {
		stickyNoteService.create("Morning Routine", "Get up with Tristan. Make Coffee. Wake up Lily and Izzy. Make Breakfast. Brush Teeth. Go to work", false);
		stickyNoteService.create("Still to do...", "Create weather scrape, using jSoup. Create photo scrape. Create bike design and price.", false);
		stickyNoteService.create("Doctor Call", "Monday 22nd at around 8-30am", true);
		stickyNoteService.create("Gardening Work Left", "Dig more soil from Zebo. Flatten front and back lawns. Seed new grass. Fix nasty bit behind shed", true);
		stickyNoteService.create("Nothing Useful", "This is just to make an extra Sticky Note as I thought 4 would look better than 3!", true);
		stickyNoteService.create("Go for a run!", "Seriously get up early and go for a run!!\nYou're just being lazy!", false);

		Frame frame1 = new Frame(GRAVEL, true, false, true);
		FrontGears frontGears1 = new FrontGears(1);
		RearGears rearGears1 = new RearGears(11);
		rearGears1.setSramGroupSet(RIVAL);
		fullBikeService.create(new FullBike("Gravel", frame1, HYDRAULIC_DISC, SRAM, FLARE, frontGears1, rearGears1, STI));

		Frame frame2 = new Frame(ROAD, false, false, true);
		FrontGears frontGears2 = new FrontGears(3);
		RearGears rearGears2 = new RearGears(9);
		rearGears2.setShimanoGroupSet(SORA);
		fullBikeService.create(new FullBike("Workhorse", frame2, RIM, SHIMANO, DROPS, frontGears2, rearGears2, STI));

		Frame frame3 = new Frame(SINGLE_SPEED, false, false, false);
		FrontGears frontGears3 = new FrontGears(1);
		RearGears rearGears3 = new RearGears(1);
		fullBikeService.create(new FullBike("Fixie", frame3, RIM, OTHER, BULLHORNS, frontGears3, rearGears3, NONE));

		Frame frame4 = new Frame(SINGLE_SPEED, true, false, false);
		FrontGears frontGears4 = new FrontGears(1);
		RearGears rearGears4 = new RearGears(1);
		fullBikeService.create(new FullBike("City Cruiser", frame4, MECHANICAL_DISC, OTHER, FLAT, frontGears4, rearGears4, NONE));
	}
}